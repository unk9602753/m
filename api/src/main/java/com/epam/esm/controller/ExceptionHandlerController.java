package com.epam.esm.controller;

import com.epam.esm.entity.ResponseClientException;
import com.epam.esm.config.Translator;
import com.epam.esm.exception.ServiceException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(InvalidDefinitionException.class)
    public ResponseEntity<ResponseClientException> handleInvalidDefinitionException(InvalidDefinitionException e) {
        ResponseClientException re = errorBody(e,Translator.toLocale("exception.time.in.body"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseClientException> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        ResponseClientException re = errorBody(e,Translator.toLocale("exception.column.null"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ResponseClientException> handleBadSqlGrammarException(BadSqlGrammarException e) {
        ResponseClientException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseClientException> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        ResponseClientException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseClientException> handleServiceException(ServiceException e) {
        ResponseClientException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST, e.getId(),e.getConcreteMessage());
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseClientException> handleNoSuchElementException(NoSuchElementException e) {
        ResponseClientException re = errorBody(e,Translator.toLocale("exception.no.values.present"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException e, HttpHeaders headers,
            HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errorMessage", Translator.toLocale("exception.parse.json"));
        body.put("errorCode", HttpServletResponse.SC_BAD_REQUEST + getErrorCodeFromClass(e));
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e,
                                                                         HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errorMessage", Translator.toLocale("ex.method.not.support"));
        body.put("errorCode", HttpServletResponse.SC_METHOD_NOT_ALLOWED + getErrorCodeFromClass(e));
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errorMessage", e.getMessage());
        body.put("errorCode", HttpServletResponse.SC_METHOD_NOT_ALLOWED + getErrorCodeFromClass(e));
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException e, HttpHeaders headers, HttpStatus status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("errorMessage", e.getMessage());
        body.put("errorCode", HttpServletResponse.SC_METHOD_NOT_ALLOWED + getErrorCodeFromClass(e));
        return new ResponseEntity<>(body, HttpStatus.METHOD_NOT_ALLOWED);
    }

    private String getErrorCodeFromClass(Throwable e) {
        String className = Arrays.stream(e.getStackTrace()).findFirst().get().getClassName();
        String temp = className.contains("Certificate") || className.contains("Tag") ? "01" : "03";
        return temp.equals("01") && className.contains("Tag") ? "02" : temp;
    }

    private ResponseClientException errorBody(Throwable e, String messageCode, int code, String ... args){
        ResponseClientException re = new ResponseClientException();
        re.setCode(code + getErrorCodeFromClass(e));
        re.setMessage(String.format(Translator.toLocale(messageCode),args));
        return re;
    }
}
