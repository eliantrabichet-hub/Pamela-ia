# Publicar Pamela AI en Render

## Incluye
- Interfaz Pamela en `src/main/resources/static/index.html`
- Backend Java / Spring Boot
- Dockerfile para Render
- Historial H2 local

## Antes de publicar
1. Verificá que la aplicación funcione localmente.
2. La clave de API NO debe ir en HTML, JavaScript, Git ni en un repositorio público.
3. Para publicar una app accesible públicamente, agregá autenticación o límites de uso para evitar que terceros consuman tu cuota de API.

## Publicación en Render
1. Subí el contenido de esta carpeta a un repositorio Git.
2. En Render, creá un servicio nuevo de tipo Web Service y conectá el repositorio.
3. Elegí Docker como runtime. Render detectará el `Dockerfile` de la raíz.
4. En Environment agregá:
   - `PAMELA_API_KEY` = tu clave privada del proveedor de IA
   - `PAMELA_API_URL` = `https://api.openai.com/v1/chat/completions` (si usás un proveedor compatible)
   - `PAMELA_API_MODEL` = el modelo disponible en tu cuenta, por ejemplo `gpt-4o-mini`
5. Iniciá el deploy y esperá a que termine.
6. Abrí la URL `onrender.com` que Render asigne al servicio.

## Memoria e historial
La base H2 se guarda en `/app/data`. Los sistemas de archivos de los servicios Render son efímeros por defecto. Para conservar el historial después de reinicios y nuevos deploys, agregá un Persistent Disk (función de pago) con mount path `/app/data`. Otra opción más escalable es migrar a una base de datos PostgreSQL administrada.

## Prueba
Abrí la URL pública y enviá un mensaje. Si falla, revisá Logs en Render y verificá que la clave y el modelo sean correctos.

## Seguridad
- No compartas ni publiques la clave API.
- No habilites H2 Console en internet.
- Antes de compartir públicamente, añadí inicio de sesión, límites por usuario, límites de gasto y protección contra abuso.
- Revisá las condiciones de edad, facturación y uso del proveedor de IA y de la plataforma de alojamiento; pedí ayuda a una persona adulta responsable si hace falta.
