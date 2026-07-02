package com.albumx.infrastructure.web;

import com.albumx.domain.exception.DomainException;
import com.albumx.domain.exception.InvalidStickerNumberException;
import com.albumx.domain.exception.InvalidUserNameException;
import com.albumx.domain.exception.SelfTradeException;
import com.albumx.domain.exception.SingleStickerProtectionException;
import com.albumx.domain.exception.StickerNotOwnedException;
import com.albumx.domain.exception.TradeAlreadyFinalizedException;
import com.albumx.domain.exception.TradeNotFoundException;
import com.albumx.domain.exception.UnauthorizedTradeActionException;
import com.albumx.domain.exception.UnfairTradeException;
import com.albumx.domain.exception.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class, TradeNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(DomainException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedTradeActionException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedTradeActionException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(TradeAlreadyFinalizedException.class)
    public ResponseEntity<ErrorResponse> handleConflict(TradeAlreadyFinalizedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler({
            InvalidUserNameException.class,
            InvalidStickerNumberException.class,
            StickerNotOwnedException.class,
            SelfTradeException.class,
            UnfairTradeException.class,
            SingleStickerProtectionException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(DomainException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse("VALIDATION_ERROR", "Dados da requisição inválidos."));
    }
}
