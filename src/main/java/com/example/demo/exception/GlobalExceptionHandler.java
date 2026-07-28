package com.example.demo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import java.util.HashMap;

//şimdi global exception her hatayı merkezden yakalamak için kullanılıyor, ozaman neden diğer 2 exception dosyasını oluşturdul
//cünkü invalidoperation ve resourcenotfound service katmanında hata fırlatmak için kullanılıyor, HATA FIRLATMAK bak
//ama bizim bu kod, fırlatılan hataları yakalayıp düzgün formata çeviren taraf, hata fırlatmıyor dinliyor, aşağılarda
//yine anlatıyorum burda netleştirmek istedm.


//peki hata fırlatmayı neden genel olarak RuntimeException olarak tekte yazmadık, çünkü bu cok genel bi hata sınıfı
//yani her türlü beklenmedik hatayı 404 not found gibi göstermiş olurduk bu bizi hem yanıltır hem de okumamızı zorlaştırırdı



//@RestControllerAdvice bu sınıf, TÜM Controller'lardan fırlatılan hataları
//merkezi olarak yakalayıp işleyecek. Her Controller'a ayrı ayrı hata yönetimi yazmamıza gerek kalmıyor
@RestControllerAdvice //bu ne demek şu demek: bu sınıf projedeki tüm controllerlara bakıyor , hata oldu mu devreye giriyor
public class GlobalExceptionHandler {

    // ResourceNotFoundException fırlatıldığında bu metot devreye girer
    @ExceptionHandler(ResourceNotFoundException.class) //"özellikle bu tip" hata fırlatırılırsa gel demek spesifik hata arıyoruz
    public ResponseEntity<Map<String, Object>> handleNotFound(ResourceNotFoundException ex) {

        //kullanıcıya dönecek JSON cevabını elle, düzenli şekilde oluşturuyoruz
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value()); // 404
        body.put("error", "Not Found");
        body.put("message", ex.getMessage()); //bizim yazdığımız mesaj, örn: "Vehicle not found with id: 5"

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }

    //InvalidOperationException fırlatıldığında bu metot devreye girer
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidOperation(InvalidOperationException ex) {
        //yukardaki kod hem http durum kodunu (404,400) hem de json bodysini aynı anda kontrol etmemizi sağlıyor

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value()); // 400
        body.put("error", "Bad Request");
        body.put("message", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
    // @Valid ile işaretlenmiş bir alan kuralı ihlal edildiğinde bu metot devreye girer
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {

        // Hangi alanda hangi hata olduğunu tek tek topluyoruz
        Map<String, String> fieldErrors = new HashMap<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("fieldErrors", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }
}