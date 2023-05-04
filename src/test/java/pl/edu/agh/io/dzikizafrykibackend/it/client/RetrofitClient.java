package pl.edu.agh.io.dzikizafrykibackend.it.client;

import pl.edu.agh.io.dzikizafrykibackend.model.*;
import retrofit2.Call;
import retrofit2.http.*;

import java.util.List;
import java.util.UUID;

public interface RetrofitClient {

    @GET("/health")
    Call<HealthResource> getHealth();

    @POST("/auth/register")
    Call<AuthenticationResponseResource> postRegister(@Body RegisterRequestResource registerRequestResource);

    @POST("/auth/authenticate")
    Call<AuthenticationResponseResource> postAuthenticate(@Body AuthenticationRequestResource authenticationRequestResource);

    @GET("/demo-security/teacher-secured-endpoint")
    Call<String> getDemoTeacherSecuredHello();

    @GET("/demo-security/student-secured-endpoint")
    Call<String> getDemoStudentSecuredHello();

    @GET("/course/")
    Call<List<Course>> getAllCourses();

    @GET("/course/{courseId}")
    Call<Course> getCourse(@Path("courseId") UUID courseId);

    @POST("/course/{courseId}")
    Call<Course> postCourse(@Body CourseUpdate course);

    @PUT("/course/{courseId}")
    Call<Course> putCourse(@Path("courseId") UUID courseId, @Body CourseUpdate course);

    @DELETE("course/{courseId}")
    Call<Void> deleteCourse(@Path("courseId") UUID courseId);

}
