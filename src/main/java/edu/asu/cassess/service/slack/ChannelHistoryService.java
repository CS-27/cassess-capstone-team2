package edu.asu.cassess.service.slack;

import edu.asu.cassess.dao.slack.IUserObjectQueryDao;
import edu.asu.cassess.model.slack.MessageList;
import edu.asu.cassess.persist.entity.rest.*;
import edu.asu.cassess.persist.entity.slack.*;
import edu.asu.cassess.persist.repo.slack.SlackMessageTotalsRepo;
import edu.asu.cassess.service.rest.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
@Transactional
public class ChannelHistoryService implements IChannelHistoryService {

    class MutableInt {
        int value = 1; // note that we start at 1 since we're counting
        public void increment () { ++value;      }
        public int  get ()       { return value; }
    }

    private RestTemplate restTemplate;

    private String channelHistoryURL;

    private Map<String, MutableInt> countsMap = new HashMap<String, MutableInt>();

    @Autowired
    private IChannelService channelService;

    @Autowired
    private ICourseService courseService;

    @Autowired
    private IUserObjectQueryDao userObjectQueryDao;

    @Autowired
    private IStudentsService studentService;

    @Autowired
    private SlackMessageTotalsRepo slackMessageTotalsRepo;

    public ChannelHistoryService() {
        restTemplate = new RestTemplate();
        channelHistoryURL = "https://slack.com/api/channels.history";
    }

    @Override
    public MessageList getMessages(String channel, String token, long unixOldest, long unixCurrent) {

        long nextUnixCurrent = 0;

        //System.out.println("----------------------------**********************************************=========UnixCurrent: " + unixCurrent);

        //System.out.println("----------------------------**********************************************=========UnixOldest: " + unixOldest);

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.APPLICATION_JSON);

        //System.out.println("Page: " + page);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<MessageList> messageList = restTemplate.getForEntity(channelHistoryURL + "?token=" + token + "&channel=" + channel +
                "&oldest=" + unixOldest + "&latest=" + unixCurrent, MessageList.class, request);

        System.out.println(messageList.getBody());

        SlackMessage[] slackMessages = messageList.getBody().getMessages();

        System.out.println(messageList.getBody());

        int index = 0;
        for(SlackMessage slackMessage:slackMessages){
            //System.out.println("----------------------------**********************************************=========Ts: " + slackMessage.getTs());
            //System.out.println("----------------------------**********************************************=========User: " + slackMessage.getUser());

            if(slackMessage.getText().length() > 20) {
                MutableInt count = countsMap.get(slackMessage.getUser());
                if (count == null) {
                    countsMap.put(slackMessage.getUser(), new MutableInt());
                } else {
                    count.increment();
                }
            }

            //slackMessageRepo.save(slackMessage);
            index++;
            if(index == (slackMessages.length -1)){
                nextUnixCurrent = (long) Math.floor(slackMessage.getTs());
                //System.out.println("----------------------------**********************************************=========NextUnixCurrent: " + nextUnixCurrent);
            }
        }

        //System.out.println("----------------------------**********************************************=========has_more: " + messageList.getBody().isHas_more());

        if (messageList.getBody().isHas_more()) {
            return getMessages(channel, token, unixOldest, nextUnixCurrent);
        } else {
            return messageList.getBody();
        }
    }

    @Override
    public void getMessageTotals(String channelID, String course, String team) {
        List<Student> students = studentService.listReadByTeam(course, team);
        for (Student student : students) {
            int messageCount = 0;
            String email = student.getEmail();
            Object object = userObjectQueryDao.getUserByEmail(email);
            if(object.getClass() == UserObject.class){
                UserObject userObject = (UserObject) object;
                if(countsMap.get(userObject.getId()) != null) {
                    messageCount = countsMap.get(userObject.getId()).get();
                }
                //System.out.println("----------------------------**********************************************=========User: " + userObject.getId());
                //System.out.println("----------------------------**********************************************=========Count: " + messageCount);
                //int messageCount = slackMessageQueryDao.getMessageCount(userObject.getId());
                slackMessageTotalsRepo.save(new SlackMessageTotals(new MessageTotalsID(userObject.getProfile().getEmail(), channelID), userObject.getProfile().getReal_name(), student.getTeam_name(), course, messageCount));
                }
            }
    }


    @Override
    public void updateMessageTotals(String course) {
        System.out.println("Updating Messages");
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        long unixOldest = c.getTimeInMillis() / 1000;
        long unixCurrent = System.currentTimeMillis() / 1000L;
        if (courseService == null) courseService = new CourseService();
        Course tempCourse = (Course) courseService.read(course);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-mm-dd");
        String formatted = df.format(new Date());
        Date current = new Date();
        try {
            current = df.parse(formatted);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (current.before(tempCourse.getEnd_date())) {
            String token = tempCourse.getSlack_token();
            if(token != null) {
                for (Team team : tempCourse.getTeams()) {
                    List<Channel> channels = channelService.listReadByTeam(team.getTeam_name(), course);
                    for (Channel channel : channels) {
                        System.out.println("Channel: " + channel.getId());
                        getMessages(channel.getId(), token, unixOldest, unixCurrent);
                        getMessageTotals(channel.getId(), course, team.getTeam_name());
                        countsMap.clear();
                    }
                }
            }
        }
    }
}
