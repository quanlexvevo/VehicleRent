package com.example.demo.exception;

//bu özel exception,"aradığımız kayıt bulunamadı" durumlarında kullanılacak
//RuntimeException'dan türüyor, yani normal Exception gibi elle try-catch zorunluluğu yok
public class ResourceNotFoundException extends RuntimeException {

    //dışarıdan sadece bir mesaj alıyoruz, üst sınıfa (RuntimeException) iletiyoruz
    public ResourceNotFoundException(String message) {
        super(message);
    }
}