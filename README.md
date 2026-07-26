# VehicleRent — Araç Kiralama REST API

Spring Boot ile geliştirilmiş, katmanlı mimari (layered architecture) prensiplerine uygun bir araç kiralama yönetim sistemi. Araç, müşteri, kiralama ve hasar kaydı işlemlerini yönetir.

## Kullanılan Teknolojiler

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring Web (Web MVC)** — REST API katmanı
- **Spring Data JPA** — veritabanı erişim katmanı
- **Hibernate** — ORM (Object-Relational Mapping)
- **H2 Database** — bellek içi (in-memory) veritabanı
- **Maven** — bağımlılık ve derleme yönetimi

## Mimari

Proje, sorumlulukları net şekilde ayırmak için 4 katmandan oluşuyor:

```
Client (Postman)
    ↓
Controller   → HTTP isteklerini karşılar, endpoint'leri tanımlar
    ↓
Service      → iş mantığını uygular (durum senkronizasyonu, fiyat hesaplama, kural kontrolleri)
    ↓
Repository   → veritabanı erişimini sağlar (Spring Data JPA)
    ↓
Veritabanı (H2)
```

Ek olarak:
- **entity** → veritabanı tablolarını temsil eden sınıflar
- **entity/enums** → sabit durum değerleri (VehicleStatus, RentalStatus, DamageSeverity)
- **dto** → Controller ile Service arasında veri taşıyan yardımcı sınıflar (Rental ve DamageRecord oluşturma istekleri için)
- **exception** → merkezi hata yönetimi (404 / 400 ayrımı yapan özel exception sınıfları)

## Klasör Yapısı

```
src/main/java/com/example/demo
├── entity
│   ├── Vehicle.java
│   ├── Customer.java
│   ├── Rental.java
│   ├── DamageRecord.java
│   └── enums
│       ├── VehicleStatus.java
│       ├── RentalStatus.java
│       └── DamageSeverity.java
├── repository
│   ├── VehicleRepository.java
│   ├── CustomerRepository.java
│   ├── RentalRepository.java
│   └── DamageRecordRepository.java
├── service
│   ├── VehicleService.java
│   ├── CustomerService.java
│   ├── RentalService.java
│   └── DamageRecordService.java
├── controller
│   ├── VehicleController.java
│   ├── CustomerController.java
│   ├── RentalController.java
│   └── DamageRecordController.java
├── dto
│   ├── CreateRentalRequest.java
│   └── CreateDamageRecordRequest.java
├── exception
│   ├── ResourceNotFoundException.java
│   ├── InvalidOperationException.java
│   └── GlobalExceptionHandler.java
└── DemoApplication.java
```

## Veri Modeli

**Vehicle (Araç):** plaka, marka, model, yıl, renk, günlük ücret, durum (`AVAILABLE`, `RENTED`, `IN_MAINTENANCE`, `DAMAGED`)

**Customer (Müşteri):** ad soyad, kimlik no, telefon, email, ehliyet numarası

**Rental (Kiralama):** araç referansı, müşteri referansı, başlangıç/bitiş tarihi, gerçek teslim tarihi, toplam ücret, durum (`ONGOING`, `COMPLETED`, `CANCELLED`)

**DamageRecord (Hasar Kaydı):** araç referansı, kiralama referansı, tarih, açıklama, şiddet (`MINOR`, `MODERATE`, `SEVERE`), tahmini onarım maliyeti, onarım durumu

## İş Kuralları (Business Logic)

- Bir araç zaten kirada, bakımda veya hasarlıysa yeni kiralamaya açılamaz.
- Yeni bir kiralama başlatıldığında ilgili aracın durumu otomatik olarak `RENTED` yapılır.
- Kiralama tamamlanıp araç teslim alındığında, aracın durumu otomatik olarak `AVAILABLE` yapılır.
- Toplam kiralama ücreti, gün sayısı × günlük ücret formülüyle otomatik hesaplanır.
- Yeni bir hasar kaydı oluşturulduğunda ilgili aracın durumu otomatik olarak `DAMAGED` yapılır.
- Hasar onarıldı olarak işaretlendiğinde araç tekrar `AVAILABLE` durumuna döner.

## Kurulum ve Çalıştırma

1. Projeyi IntelliJ IDEA ile açın (Maven projesi olarak otomatik tanınır).
2. `src/main/java/.../DemoApplication.java` dosyasını çalıştırın (Run).
3. Uygulama `http://localhost:8080` adresinde ayağa kalkar.
4. H2 veritabanı konsoluna `http://localhost:8080/h2-console` adresinden erişebilirsiniz:
   - JDBC URL: `jdbc:h2:mem:testdb`
   - Kullanıcı adı: `sa`
   - Şifre: *(boş)*

> **Not:** H2 bellek içi (in-memory) çalıştığı için, uygulama her yeniden başlatıldığında tüm veriler sıfırlanır.

## API Endpoint Listesi

### Vehicle (Araç) — `/vehicles`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/vehicles` | Tüm araçları listeler |
| GET | `/vehicles/{id}` | Belirli bir aracı getirir |
| POST | `/vehicles` | Yeni araç ekler |
| PUT | `/vehicles/{id}` | Araç bilgilerini günceller |
| DELETE | `/vehicles/{id}` | Aracı siler |

**Örnek POST Body:**
```json
{
  "plate": "34ABC123",
  "brand": "Toyota",
  "model": "Corolla",
  "year": 2022,
  "color": "Beyaz",
  "dailyRate": 1500.0,
  "status": "AVAILABLE"
}
```

### Customer (Müşteri) — `/customers`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/customers` | Tüm müşterileri listeler |
| GET | `/customers/{id}` | Belirli bir müşteriyi getirir |
| POST | `/customers` | Yeni müşteri ekler |
| PUT | `/customers/{id}` | Müşteri bilgilerini günceller |
| DELETE | `/customers/{id}` | Müşteriyi siler |

**Örnek POST Body:**
```json
{
  "fullName": "Ahmet Yilmaz",
  "nationalId": "12345678901",
  "phone": "05551234567",
  "email": "ahmet@mail.com",
  "licenseNumber": "B123456"
}
```

### Rental (Kiralama) — `/rentals`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/rentals` | Tüm kiralama kayıtlarını listeler |
| GET | `/rentals/{id}` | Belirli bir kiralamayı getirir |
| POST | `/rentals` | Yeni kiralama başlatır |
| PUT | `/rentals/{id}/return` | Aracı teslim alır, kiralamayı tamamlar |
| DELETE | `/rentals/{id}` | Kiralama kaydını siler |

**Örnek POST Body:**
```json
{
  "vehicleId": 1,
  "customerId": 1,
  "startDate": "2026-07-24",
  "endDate": "2026-07-28"
}
```

### DamageRecord (Hasar Kaydı) — `/damage-records`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/damage-records` | Tüm hasar kayıtlarını listeler |
| GET | `/damage-records/{id}` | Belirli bir hasar kaydını getirir |
| GET | `/damage-records/vehicle/{vehicleId}` | Belirli bir araca ait hasar geçmişini listeler |
| POST | `/damage-records` | Yeni hasar kaydı oluşturur |
| PUT | `/damage-records/{id}/repair` | Hasarı onarıldı olarak işaretler |
| DELETE | `/damage-records/{id}` | Hasar kaydını siler |

**Örnek POST Body:**
```json
{
  "vehicleId": 1,
  "rentalId": 1,
  "description": "Sol on camurlukta cizik",
  "severity": "MINOR",
  "estimatedRepairCost": 500.0
}
```

## Hata Yönetimi

API, iki tür özel hata döner:

**404 Not Found** — istenen kayıt veritabanında bulunamadığında:
```json
{
  "timestamp": "2026-07-24T15:01:50.921",
  "status": 404,
  "error": "Not Found",
  "message": "Vehicle not found with id: 999"
}
```

**400 Bad Request** — kayıt var ama işlem iş kuralına aykırı olduğunda (örn. zaten kirada olan bir aracı kiralamaya çalışmak):
```json
{
  "timestamp": "2026-07-24T15:01:50.921",
  "status": 400,
  "error": "Bad Request",
  "message": "Vehicle is not available for rental"
}
```

## Örnek Test Akışı (Postman)

1. `POST /vehicles` → yeni araç ekle
2. `POST /customers` → yeni müşteri ekle
3. `POST /rentals` → kiralama başlat (araç durumu otomatik `RENTED` olur)
4. `GET /vehicles/{id}` → aracın durumunu doğrula
5. `PUT /rentals/{id}/return` → aracı teslim al (araç durumu otomatik `AVAILABLE` olur)
6. `POST /damage-records` → hasar kaydı oluştur (araç durumu otomatik `DAMAGED` olur)
7. `PUT /damage-records/{id}/repair` → hasarı onar (araç durumu otomatik `AVAILABLE` olur)


