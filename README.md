# VehicleRent — Araç Kiralama REST API

Spring Boot ile geliştirilmiş, katmanlı mimari (layered architecture) prensiplerine uygun bir araç kiralama yönetim sistemi. Araç, müşteri, kiralama ve hasar kaydı işlemlerini yönetir.

## Kullanılan Teknolojiler

- **Java 17**
- **Spring Boot 4.1.0**
- **Spring Web (Web MVC)** — REST API katmanı
- **Spring Data JPA** — veritabanı erişim katmanı
- **Hibernate** — ORM (Object-Relational Mapping)
- **H2 Database** — dosya tabanlı (file-based), kalıcı veritabanı
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

**Vehicle (Araç):** plaka, marka, model, yıl, renk, günlük ücret, durum (`AVAILABLE`, `RENTED`, `DAMAGED`)

**Customer (Müşteri):** ad soyad, kimlik no, telefon, email, ehliyet numarası

**Rental (Kiralama):** araç referansı (`vehicle_id`), müşteri referansı (`customer_id`), başlangıç/bitiş tarihi, gerçek teslim tarihi, toplam ücret, durum (`ONGOING`, `COMPLETED`, `CANCELLED`)

**DamageRecord (Hasar Kaydı):** araç referansı (`vehicle_id`), kiralama referansı (`rental_id`), tarih, açıklama, şiddet (`MINOR`, `MODERATE`, `SEVERE`), tahmini onarım maliyeti, onarım durumu

## Hangi Endpoint'te Hangi ID Kullanılıyor? (Önemli)

Projede 3 farklı "id" kavramı var — `Vehicle.id`, `Rental.id`, `DamageRecord.id` — ve bunlar birbirinin yerine **geçmez**. Bir endpoint'in URL'sindeki `{id}` her zaman o Controller'ın ait olduğu tabloyu işaret eder, ilişkili diğer tabloları değil. Karışıklığı önlemek için tek tek özetliyoruz:

| Endpoint | `{id}` neyin id'si? | Sistem diğer kayda nasıl ulaşır? |
|----------|----------------------|-----------------------------------|
| `GET /vehicles/{id}` | **Vehicle**'ın id'si | — |
| `PUT /vehicles/{id}` | **Vehicle**'ın id'si | — |
| `DELETE /vehicles/{id}` | **Vehicle**'ın id'si | — |
| `GET /rentals/{id}` | **Rental**'ın id'si | — |
| `PUT /rentals/{id}/return` | **Rental**'ın id'si (araç id'si DEĞİL) | Sistem, `rental.getVehicle()` ile ilgili aracı kiralama kaydının içinden kendisi bulur |
| `DELETE /rentals/{id}` | **Rental**'ın id'si | — |
| `GET /damage-records/{id}` | **DamageRecord**'un id'si | — |
| `PUT /damage-records/{id}/repair` | **DamageRecord**'un id'si (araç id'si DEĞİL) | Sistem, `damageRecord.getVehicle()` ile ilgili aracı hasar kaydının içinden kendisi bulur |
| `DELETE /damage-records/{id}` | **DamageRecord**'un id'si | — |
| `GET /damage-records/vehicle/{vehicleId}` | **Vehicle**'ın id'si (tek istisna: parametre adı da `vehicleId`) | Bu endpoint özellikle "bu araca ait tüm hasarları getir" için var, o yüzden burada gerçekten araç id'si isteniyor |

**Kısaca hatırlaman gereken kural:** `/return` ve `/repair` gibi bir alt-eylem (`sub-action`) içeren endpoint'lerde URL'deki id, her zaman o Controller'ın kendi kaydına (Rental veya DamageRecord) aittir — araca değil. Araç bilgisi, o kayıtların içindeki referans üzerinden **otomatik olarak** bulunur, sen ayrıca araç id'si vermezsin.

## İş Kuralları (Business Logic)

- Bir araç zaten kirada veya hasarlıysa yeni kiralamaya açılamaz.
- Yeni bir kiralama başlatıldığında ilgili aracın durumu otomatik olarak `RENTED` yapılır.
- Kiralama tamamlanıp araç teslim alındığında, araç **hasarlı değilse** durumu otomatik olarak `AVAILABLE` yapılır. Araç hasarlıysa (`DAMAGED`), teslim alma işlemi bu durumu değiştirmez — kiralama `COMPLETED` olur ama araç onarılana kadar `DAMAGED` kalır.
- Toplam kiralama ücreti, gün sayısı × günlük ücret formülüyle otomatik hesaplanır.
- Aynı plakaya sahip iki araç eklenemez (`Vehicle.plate` alanı eşsiz olmalıdır).
- Yeni bir hasar kaydı oluşturulduğunda ilgili aracın durumu otomatik olarak `DAMAGED` yapılır.
- Hasar onarıldı olarak işaretlendiğinde araç tekrar `AVAILABLE` durumuna döner.

## Kurulum ve Çalıştırma

1. Projeyi IntelliJ IDEA ile açın (Maven projesi olarak otomatik tanınır).
2. `src/main/java/.../DemoApplication.java` dosyasını çalıştırın (Run).
3. Uygulama `http://localhost:8080` adresinde ayağa kalkar.
4. H2 veritabanı konsoluna `http://localhost:8080/h2-console` adresinden erişebilirsiniz:
   - JDBC URL: `jdbc:h2:file:./data/vehiclerentdb`
   - Kullanıcı adı: `sa`
   - Şifre: *(boş)*

> **Not:** H2 dosya tabanlı (file) modda çalıştığı için veriler `data/` klasöründe kalıcı olarak saklanır. Uygulamayı kapatıp açsan bile veriler kaybolmaz.

## API Endpoint Listesi

### Vehicle (Araç) — `/vehicles`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/vehicles` | Tüm araçları listeler |
| GET | `/vehicles/{id}` | Belirli bir aracı getirir (`id` = Vehicle id) |
| POST | `/vehicles` | Yeni araç ekler |
| PUT | `/vehicles/{id}` | Araç bilgilerini günceller (`id` = Vehicle id) |
| DELETE | `/vehicles/{id}` | Aracı siler (`id` = Vehicle id) |

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
| GET | `/customers/{id}` | Belirli bir müşteriyi getirir (`id` = Customer id) |
| POST | `/customers` | Yeni müşteri ekler |
| PUT | `/customers/{id}` | Müşteri bilgilerini günceller (`id` = Customer id) |
| DELETE | `/customers/{id}` | Müşteriyi siler (`id` = Customer id) |

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
| GET | `/rentals/{id}` | Belirli bir kiralamayı getirir (`id` = Rental id) |
| POST | `/rentals` | Yeni kiralama başlatır (Body içinde `vehicleId` ve `customerId` gönderilir) |
| PUT | `/rentals/{id}/return` | Aracı teslim alır, kiralamayı tamamlar. **`id` = Rental id, Vehicle id DEĞİL.** Body göndermene gerek yok |
| DELETE | `/rentals/{id}` | Kiralama kaydını siler (`id` = Rental id) |

**Örnek POST Body:**
```json
{
  "vehicleId": 1,
  "customerId": 1,
  "startDate": "2026-07-24",
  "endDate": "2026-07-28"
}
```
> Not: `vehicleId` ve `customerId`, bu iki alanın **tek istisna olduğu yer** — burada gerçekten Vehicle ve Customer id'leri gönderiliyor, çünkü yeni bir Rental henüz yokken hangi araç/müşteriyle ilişkilendirileceğini belirtmemiz gerekiyor.

### DamageRecord (Hasar Kaydı) — `/damage-records`

| Metot | Endpoint | Açıklama |
|-------|----------|----------|
| GET | `/damage-records` | Tüm hasar kayıtlarını listeler |
| GET | `/damage-records/{id}` | Belirli bir hasar kaydını getirir (`id` = DamageRecord id) |
| GET | `/damage-records/vehicle/{vehicleId}` | Belirli bir araca ait hasar geçmişini listeler (`vehicleId` = Vehicle id — bu endpoint'te gerçekten araç id'si kullanılır) |
| POST | `/damage-records` | Yeni hasar kaydı oluşturur (Body içinde `vehicleId` ve `rentalId` gönderilir) |
| PUT | `/damage-records/{id}/repair` | Hasarı onarıldı olarak işaretler. **`id` = DamageRecord id, Vehicle id DEĞİL.** Body göndermene gerek yok |
| DELETE | `/damage-records/{id}` | Hasar kaydını siler (`id` = DamageRecord id) |

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
> Not: `vehicleId` ve `rentalId` de aynı istisna — yeni bir DamageRecord oluştururken hangi araç ve hangi kiralamayla ilişkilendirileceğini belirtmek için kullanılıyor.

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

**400 Bad Request** — kayıt var ama işlem iş kuralına aykırı olduğunda (örn. zaten kirada olan bir aracı kiralamaya çalışmak, ya da aynı plakayla ikinci bir araç eklemeye çalışmak):
```json
{
  "timestamp": "2026-07-24T15:01:50.921",
  "status": 400,
  "error": "Bad Request",
  "message": "Vehicle is not available for rental"
}
```

## Örnek Test Akışı (Postman)

1. `POST /vehicles` → yeni araç ekle, dönen **Vehicle id**'yi not al
2. `POST /customers` → yeni müşteri ekle, dönen **Customer id**'yi not al
3. `POST /rentals` → kiralama başlat (Body'de Vehicle id ve Customer id'yi kullan), dönen **Rental id**'yi not al — araç durumu otomatik `RENTED` olur
4. `GET /vehicles/{vehicleId}` → aracın durumunu doğrula
5. `PUT /rentals/{rentalId}/return` → aracı teslim al (**Rental id** kullan) — araç durumu otomatik `AVAILABLE` olur
6. `POST /damage-records` → hasar kaydı oluştur (Body'de Vehicle id ve Rental id'yi kullan), dönen **DamageRecord id**'yi not al — araç durumu otomatik `DAMAGED` olur
7. `PUT /damage-records/{damageRecordId}/repair` → hasarı onar (**DamageRecord id** kullan) — araç durumu otomatik `AVAILABLE` olur

## Geliştirmeye Açık Noktalar

- Kimlik doğrulama ve yetkilendirme (Spring Security + JWT)
- Sayfalama (pagination) ve filtreleme
- Unit ve entegrasyon testleri
- Tarih çakışması kontrolü (aynı aracın aynı tarih aralığında iki kez kiralanmasını engelleme — şu an sadece araç durumu üzerinden dolaylı bir koruma var, gerçek tarih bazlı kontrol yok)
