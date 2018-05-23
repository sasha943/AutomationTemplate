package com.qa.restApi;

import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.io.File;
import java.net.URI;
import java.util.Map;

import static io.restassured.RestAssured.given;

public abstract class RestAssuredRequests {

    /**
     * GET METHODS
     */

    ExtractableResponse<Response> executeGet(RequestSpecification spec, URI uri, int statusCode) {
        return given()
                .spec(spec)
                .when()
                .get(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    ExtractableResponse<Response> executeGetWithParameters(RequestSpecification spec, URI uri, Map<String, String> parameters, int statusCode) {
        return given()
                .spec(spec)
                .parameters(parameters)
                .when()
                .get(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    ExtractableResponse<Response> executeGetWithToken(RequestSpecification spec, URI uri, String token, int statusCode) {
        return given()
                .spec(spec)
                .auth().oauth2(token)
                .when()
                .get(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    /**
     * POST METHODS
     */

    ExtractableResponse<Response> executePost(RequestSpecification spec, URI uri, Map<String, String> formParams, int statusCode) {
        return given()
                .spec(spec)
                .contentType(ContentType.URLENC)
                .formParams(formParams)
                .when()
                .post(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    ExtractableResponse<Response> executePostFile(RequestSpecification spec, URI uri, String filePath, int statusCode) {
        return given()
                .spec(spec)
                .multiPart(new File(filePath))
                .when()
                .post(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    ExtractableResponse<Response> executePostWithJSONString(RequestSpecification spec, URI uri, String body, int statusCode) {
        return given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .post(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    /**
     * PUT METHODS
     */

    ExtractableResponse<Response> executePutWithJSONString(RequestSpecification spec, URI uri, String body, int statusCode) {
        return given()
                .spec(spec)
                .contentType(ContentType.JSON)
                .body(body)
                .when()
                .put(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

    ExtractableResponse<Response> executeDelete(RequestSpecification spec, URI uri, int statusCode) {
        return given()
                .spec(spec)
                .when()
                .delete(uri)
                .then()
                .assertThat()
                .statusCode(statusCode)
                .and()
                .extract();
    }

}
