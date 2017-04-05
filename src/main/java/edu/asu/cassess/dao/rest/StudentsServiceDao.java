package edu.asu.cassess.dao.rest;

import edu.asu.cassess.persist.entity.rest.Student;
import edu.asu.cassess.persist.entity.rest.RestResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import edu.asu.cassess.persist.entity.security.User;
import edu.asu.cassess.persist.repo.UserRepo;
import edu.asu.cassess.persist.repo.UsersAuthorityRepo;
import edu.asu.cassess.persist.repo.rest.StudentRepo;
import edu.asu.cassess.service.security.UserService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;


/**
 * REST API implementation for student-based services.
 * 
 * @author tjjohn1
 *
 */
@Component
@Transactional
public class StudentsServiceDao {

    @Autowired
    private StudentRepo studentRepo;

        protected EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

<<<<<<< HEAD
    /**
     * Adds this student to the database.
     * 
     * @param student the student to add
     * @return the student passed or message if student already exists
     */
    @Transactional
=======
>>>>>>> master
    public <T> Object create(Student student) {
        System.out.println("Got into create");
        if(studentRepo.findOne(student.getEmail()) != null){
            return new RestResponse(student.getEmail() + " already exists in database");
        }else{
            studentRepo.save(student);
            return student;
        }
    }

<<<<<<< HEAD
    /**
     * Update the database entry for this student with new information.
     * 
     * @param student the student to update
     * @return the student passed or message if no such student exists in database
     */
    @Transactional
=======
>>>>>>> master
    public <T> Object update(Student student) {
        if(studentRepo.findOne(student.getEmail()) != null){
            studentRepo.save(student);
            return student;
        }else{
            return new RestResponse(student.getEmail() + " does not exist in database");
        }
    }
<<<<<<< HEAD
    
    /**
     * Finds student in database by email address.
     * 
     * @param email the string (email address) to search
     * @return the student or a message if the student is not found
     */
    @Transactional
=======

>>>>>>> master
    public <T> Object find(String email) {
        Student student = studentRepo.findOne(email);
        if(student != null){
            return student;
        }else{
            return new RestResponse(email + " does not exist in database");
        }
    }

<<<<<<< HEAD
    /**
     * Delete the student record from the database associated with this email address.
     * 
     * @param email the string (email address) used to find student
     * @return a message indicating success or no such database object
     */
    @Transactional
=======
>>>>>>> master
    public <T> Object delete(String email) {
        Student student = studentRepo.findOne(email);
        if(student != null){
            studentRepo.delete(student);
            return new RestResponse(email + " has been removed from the database");
        }else{
            return new RestResponse(email + " does not exist in the database");
        }
    }
<<<<<<< HEAD
    
    /**
     * List of all students.
     * 
     * @return a List of all unique rows in the students table
     * @throws DataAccessException
     */
    @Transactional
=======

>>>>>>> master
    public List<Student> listReadAll() throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.students", Student.class);
        List<Student> resultList = query.getResultList();
        return resultList;
    }

<<<<<<< HEAD
    /**
     * List of students constrained by the course name provided.
     * 
     * @param course the course name to filter by
     * @return a List of students in the course
     * @throws DataAccessException
     */
    @Transactional
    public List<Student> listReadByCourse(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.students WHERE course = ?1", Student.class);
        query.setParameter(1, course);
        List<Student> resultList = query.getResultList();
        return resultList;
    }
    
    /**
     * List of students constrained by the project name provided.
     * 
     * @param project_name the project name to filter by
     * @return a List of students in the project
     * @throws DataAccessException
     */
    @Transactional
    public List<Student> listReadByProject(String project_name) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.students WHERE project_name = ?1", Student.class);
        query.setParameter(1, project_name);
        List<Student> resultList = query.getResultList();
        return resultList;
    }
    
    /**
     * List of students based on the Taiga slug.
     * 
     * @param slug the Taiga slug to filter by
     * @return a List of students associated with the Taiga slug
     * @throws DataAccessException
     */
    @Transactional
    public List<Student> listReadBySlug(String slug) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.students WHERE taiga_project_slug = ?1", Student.class);
        query.setParameter(1, slug);
        List<Student> resultList = query.getResultList();
        return resultList;
    }

    /**
     * List of Taiga slugs associated with a course name.
     * 
     * @param course the course name to filter by
     * @return a List of Taiga slugs associated with course
     * @throws DataAccessException
     */
    @Transactional
    public List<Slugs> listGetSlugs(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT taiga_project_slug FROM cassess.students WHERE course = ?1", Slugs.class);
        query.setParameter(1, course);
        List<Slugs> resultList = query.getResultList();
        return resultList;
    }
    
    /**
     * List of teams associated with a course name.
     * 
     * @param course the course name to filter by
     * @return a List of project names associated with course
     * @throws DataAccessException
     */
    @Transactional
    public List<Teams> listGetProjectNames(String course) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT project_name AS 'team' FROM cassess.students WHERE course = ?1", Teams.class);
        query.setParameter(1, course);
        List<Teams> resultList = query.getResultList();
        return resultList;
    }

    /**
     * Create a set of student entities in the database.
     * 
     * @param students List of student objects
     * @return JSONObject of student information or message indicating entity already exists
     */
    @Transactional
=======
    public List<Student> listReadByTeam(String team_name) throws DataAccessException {
        Query query = getEntityManager().createNativeQuery("SELECT DISTINCT * FROM cassess.students WHERE team_name = ?1", Student.class);
        query.setParameter(1, team_name);
        List<Student> resultList = query.getResultList();
        return resultList;
    }

>>>>>>> master
    public JSONObject listCreate(List<Student> students) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        JSONArray successArray = new JSONArray();
        JSONArray failureArray = new JSONArray();
        for(Student student:students)
            if(studentRepo.findOne(student.getEmail()) != null){
                try {
                    failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(student.getEmail() + " already exists in database"))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }else{
                studentRepo.save(student);
                try {
                    successArray.put(new JSONObject(ow.writeValueAsString(student)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("success", successArray);
        returnJSON.put("failure", failureArray);
        return returnJSON;
    }

<<<<<<< HEAD
    /**
     * Update student entities in database with information from students List.
     * 
     * @param students List of student objects
     * @return JSONObject of student information or message indicating entity does not exist
     */
    @Transactional
=======
>>>>>>> master
    public JSONObject listUpdate(List<Student> students) {
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        JSONArray successArray = new JSONArray();
        JSONArray failureArray = new JSONArray();
        for (Student student : students) {
            if (studentRepo.findOne(student.getEmail()) == null) {
                try {
                    failureArray.put(new JSONObject(ow.writeValueAsString(new RestResponse(student.getEmail() + " does not exist in database"))));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            } else {
                studentRepo.save(student);
                try {
                    successArray.put(new JSONObject(ow.writeValueAsString(student)));
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        JSONObject returnJSON = new JSONObject();
        returnJSON.put("success", successArray);
        returnJSON.put("failure", failureArray);
        return returnJSON;
    }
<<<<<<< HEAD
    
    /**
     * Delete all student entities in database associated with this course.
     * 
     * @param course name of course to filter by
     * @return message indicating students removed or no students found for course
     */
    @Transactional
    public <T> Object deleteByCourse(String course) {
        Query preQuery = getEntityManager().createNativeQuery("SELECT * FROM cassess.students WHERE course = ?1 LIMIT 1", Student.class);
        preQuery.setParameter(1, course);
        Student student = (Student) preQuery.getSingleResult();
        if(student != null){
            Query query = getEntityManager().createNativeQuery("DELETE FROM cassess.students WHERE course = ?1");
            query.setParameter(1, course);
            query.executeUpdate();
            return new RestResponse("All students in course " + course + " have been removed from the database");
        }else{
            return new RestResponse("No students in course " + course + " exist in the database");
        }
    }

    /**
     * Delete all student entities in database associated with this project name.
     * 
     * @param project_name name of the project to filter by
     * @return message indicating students removed or no students found for project_name
     */
    @Transactional
    public <T> Object deleteByProject(String project_name) {
        Query preQuery = getEntityManager().createNativeQuery("SELECT * FROM cassess.students WHERE project_name = ?1 LIMIT 1", Student.class);
        preQuery.setParameter(1, project_name);
=======

    public <T> Object deleteByTeam(String team_name) {
        Query preQuery = getEntityManager().createNativeQuery("SELECT * FROM cassess.students WHERE team_name = ?1 LIMIT 1", Student.class);
        preQuery.setParameter(1, team_name);
>>>>>>> master
        Student student = (Student) preQuery.getSingleResult();
        if(student != null){
            Query query = getEntityManager().createNativeQuery("DELETE FROM cassess.students WHERE team_name = ?1");
            query.setParameter(1, team_name);
            query.executeUpdate();
            return new RestResponse("All students in project " + team_name + " have been removed from the database");
        }else{
            return new RestResponse("No students in project " + team_name + " exist in the database");
        }
    }

}