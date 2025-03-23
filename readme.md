# Ilmastiku-põhine Tarneteenuse Rakendus

See rakendus on loodud Fujitsu proovitöö osana ning võimaldab arvutada tarnetasusid vastavalt valitud linnale, sõidukitüübile ja hetkel valitsevatele ilmastikutingimustele.

## Funktsionaalsus

Rakendus sisaldab järgmisi komponente:

1. **Andmebaas** - ilmastikuandmete hoidmiseks
2. **CronJob** - ilmastikuandmete automaatseks uuendamiseks Eesti Ilmateenistuse API-st
3. **Tarnetasu arvutus** - võtab arvesse linna, sõidukitüübi ja ilmastikutingimused
4. **REST API** - võimaldab teha päringuid tarnetasude arvutamiseks ja ärireeglite haldamiseks

## Ärireeglid

### Piirkondlikud baastasud (RBF)

| Linn | Auto | Tõukeratas | Jalgratas |
|------|------|------------|-----------|
| Tallinn | 4€ | 3,5€ | 3€ |
| Tartu | 3,5€ | 3€ | 2,5€ |
| Pärnu | 3€ | 2,5€ | 2€ |

### Ilmastiku lisatasud

#### Õhutemperatuuri lisatasu (ATEF)
* Rakendub tõukeratastele ja jalgratastele
* Temperatuur alla -10°C: +1€
* Temperatuur -10°C kuni 0°C vahel: +0,5€

#### Tuule kiiruse lisatasu (WSEF)
* Rakendub ainult jalgratastele
* Tuule kiirus 10 m/s kuni 20 m/s: +0,5€
* Tuule kiirus üle 20 m/s: sõiduk keelatud

#### Ilmastikunähtuste lisatasu (WPEF)
* Rakendub tõukeratastele ja jalgratastele
* Lume- või lörtsisadu: +1€
* Vihmasadu: +0,5€
* Jäide, rahe või äike: sõiduk keelatud

## Tarnetasu näidisarvutus

* Sisend: TARTU ja JALGRATAS -> RBF = 2,5€
* Ilmastikuandmed Tartus:
  * Õhutemperatuur = -2,1°C -> ATEF = 0,5€
  * Tuule kiirus = 4,7 m/s -> WSEF = 0€
  * Ilmastikunähtus = Kerge lumesadu -> WPEF = 1€
* Kokku: RBF + ATEF + WSEF + WPEF = 2,5 + 0,5 + 0 + 1 = 4€

## Käivitamine

### Nõuded
* Java 17+ (JDK)
* Maven

### Sammud
1. Klooni projekt
2. Käivita käsuga `mvn spring-boot:run või jooksutada koodiredaktoris faili DeliveryApplication.java`
3. Rakendus käivitub pordil 8081

## API

### Tarnetasu arvutamine

```
POST /api/delivery-fee/calculate
```

Sisend:
```json
{
  "city": "TALLINN",
  "vehicleType": "BIKE"
}
```

Väljund:
```json
{
  "fee": 4.5,
  "error": null,
  "breakdown": {
    "regionalBaseFee": 3.0,
    "airTemperatureExtraFee": 0.5,
    "windSpeedExtraFee": 0.0,
    "weatherPhenomenonExtraFee": 1.0
  }
}
```

### Ärireeglite haldamine (boonusfunktsionaalsus)

Rakendus võimaldab REST API kaudu hallata piirkondlikke baastasusid ja ilmastiku lisatasusid. Vastavad end pointid on dokumenteeritud Swagger UI-s.

## Dokumentatsioon

Täielik API dokumentatsioon on saadaval Swagger UI kaudu aadressil:
```
http://localhost:8081/swagger-ui.html
```

## Tehnoloogiad

* Spring Boot 3.4
* Spring Data JPA
* H2 andmebaas
* Swagger/OpenAPI dokumentatsioon
* JUnit 5 testimine
