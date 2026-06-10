# Similar Products (prueba técnica backend)

API en Spring Boot. Le pasas un producto y te devuelve el detalle de los similares.

Necesitas Java 21 y Docker.

## Arrancar

```bash
docker-compose up -d simulado influxdb grafana
./mvnw spring-boot:run
```

- App mía → http://localhost:5000
- Mocks del enunciado → http://localhost:3001

Prueba rápida: `GET http://localhost:5000/product/1/similar`

## Test de carga (k6)

Con la app levantada:

```bash
docker-compose run --rm k6 run scripts/test.js
```

Gráficas en http://localhost:3000/d/Le2Ku9NMk/k6-performance-test

## Tests

```bash
./mvnw test
```

## Qué hace

No guardo datos yo. Solo llamo a las dos APIs que vienen en el enunciado:

1. Sacar los ids similares
2. Pedir el detalle de cada uno
3. Devolver la lista en orden

Producto que no existe → 404.  
API de productos caído → 502.

## Apaños que hice

Los mocks del enunciado tienen productos que tardan un montón (hasta 50 segundos). Si espero a todos, la API se muere. Así que corto a los 2 segundos y si uno no llega, devuelvo los demás.

Las llamadas a detalle van en paralelo, no una detrás de otra. Con 200 usuarios pegándole a la vez, en serie sería un desastre.

Puse caché de 30s porque el test de carga repite los mismos productos una y otra vez. También cacheo los fallos: si un producto da timeout, no quiero esperar 2s en cada petición.

Los mensajes de error los dejé en un solo sitio para no tener textos repetidos por ahí.

