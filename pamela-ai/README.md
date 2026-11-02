# Pamela AI

Asistente de programación con interfaz web, backend Java/Spring Boot, API de IA e historial persistente en H2.

## Requisitos

- Java 17 o superior
- Maven 3.9 o superior
- Una clave API del proveedor de IA configurado

## Configurar la API

La clave debe permanecer en el servidor, nunca en `script.js` ni en el HTML.

Linux/macOS:
```bash
export PAMELA_API_KEY="tu_clave_api"
export PAMELA_API_MODEL="gpt-4o-mini"
```

Windows PowerShell:
```powershell
$env:PAMELA_API_KEY="tu_clave_api"
$env:PAMELA_API_MODEL="gpt-4o-mini"
```

El proyecto usa por defecto el endpoint compatible de OpenAI Chat Completions. Si tu proveedor ofrece un endpoint compatible, podés cambiar `PAMELA_API_URL` y `PAMELA_API_MODEL`.

## Ejecutar

Desde la carpeta del proyecto:
```bash
mvn spring-boot:run
```

Abrí en el navegador:
http://localhost:8080

## Cómo funciona la memoria

- El navegador conserva el identificador de conversación en `localStorage`.
- Java guarda los mensajes en una base H2 local (`./data/pamela-db`).
- El endpoint `/api/history/{conversationId}` recupera los últimos mensajes.
- El modelo recibe los últimos 30 mensajes disponibles como contexto.

La memoria es por conversación y está guardada en la computadora/servidor donde ejecutás Pamela. No es una memoria ilimitada.

## Endpoints

- `POST /api/chat` con JSON: `{"conversationId":"id-opcional","message":"Hola Pamela"}`
- `GET /api/history/{conversationId}`

## Antes de publicar en internet

Este es un punto de partida local. Antes de exponerlo públicamente, agregá autenticación, límites de uso, protección contra abuso, HTTPS, política de privacidad y controles de costos. No habilites la consola H2 en producción. Nunca pongas la clave API en el frontend.
