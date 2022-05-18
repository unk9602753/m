package com.epam.esm.controller;

import com.epam.esm.entity.ResponseException;
import com.epam.esm.config.Translator;
import com.epam.esm.exception.ServiceException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

import javax.servlet.http.HttpServletResponse;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Arrays;
import java.util.NoSuchElementException;

@ControllerAdvice
public class ExceptionHandlerController {

    @ExceptionHandler(InvalidDefinitionException.class)
    public ResponseEntity<ResponseException> handleInvalidDefinitionException(InvalidDefinitionException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.time.in.body"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<ResponseException> handleJsonParseException(JsonParseException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.parse.json"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public ResponseEntity<ResponseException> handleSQLIntegrityConstraintViolationException(SQLIntegrityConstraintViolationException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.column.null"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BadSqlGrammarException.class)
    public ResponseEntity<ResponseException> handleBadSqlGrammarException(BadSqlGrammarException e) {
        ResponseException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmptyResultDataAccessException.class)
    public ResponseEntity<ResponseException> handleEmptyResultDataAccessException(EmptyResultDataAccessException e) {
        ResponseException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_NOT_FOUND);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ResponseException> handleServiceException(ServiceException e) {
        ResponseException re = errorBody(e,e.getMessage(),HttpServletResponse.SC_BAD_REQUEST, e.getId(),e.getConcreteMessage());
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ResponseException> handleNoSuchElementException(NoSuchElementException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.no.values.present"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    private String getErrorCodeFromClass(Throwable e) {
        String className = Arrays.stream(e.getStackTrace()).findFirst().get().getClassName();
        String temp = className.contains("Certificate") || className.contains("Tag") ? "01" : "03";
        return temp.equals("01") && className.contains("Tag") ? "02" : temp;
    }

    private ResponseException errorBody(Throwable e, String messageCode, int code, String ... args){
        ResponseException re = new ResponseException();
        re.setCode(code + getErrorCodeFromClass(e));
        re.setMessage(String.format(Translator.toLocale(messageCode),args));
        return re;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ResponseException> handleHttpMessageNotReadable(HttpMessageNotReadableException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.HttpMessageNotReadableException"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ResponseException> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.HttpRequestMethodNotSupportedException"),HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        return new ResponseEntity<>(re,HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ResponseException> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.HttpMediaTypeNotSupportedException"),HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
        return new ResponseEntity<>(re,HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ResponseException> handleMissingServletRequestParameter(MissingServletRequestParameterException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.no.values.present"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ResponseException> handleTypeMismatch(TypeMismatchException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.TypeMismatchException"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ResponseException> handleHttpMessageNotWritable(HttpMessageNotWritableException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.HttpMessageNotWritableException"),HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(re,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MissingServletRequestPartException.class)
    public ResponseEntity<ResponseException> handleMissingServletRequestPart(MissingServletRequestPartException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.MissingServletRequestParameterException"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ResponseException> handleBindException(BindException e) {
        ResponseException re = errorBody(e,Translator.toLocale("exception.bind"),HttpServletResponse.SC_BAD_REQUEST);
        return new ResponseEntity<>(re,HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConversionNotSupportedException.class)
    public ResponseEntity<ResponseException> handleConversionNotSupportedException(ConversionNotSupportedException e) {
        ResponseException re = errorBody(e, Translator.toLocale("exception.ConversionNotSupportedException"), HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(re, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}