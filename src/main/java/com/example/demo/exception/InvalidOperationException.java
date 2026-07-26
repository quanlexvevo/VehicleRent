package com.example.demo.exception;

//bu özel exception, "kayıt bulunamadı" değil ama "iş kuralına aykırı bir istek" durumlarında kullanılacak
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}

//niye ayrı exception sınıfı yazdık tekte olmaz mıydı, hayır olmazdı cünkü http de farklı anlamlar taşıyor
//404 kayıt yok diyor, 400 bad request ise aslında adından biraz belli, hani kayıt var ama mantığın yanlıs
//örn: zaten kirada olan bir aracı tekrar kiralamaya çalışmak gibi