HabitAPI 🛡️✅
HabitAPI es una solución full-stack para la gestión de hábitos, implementada como un monolito robusto con Spring Boot 3 y Angular. El proyecto destaca por su despliegue automatizado mediante Docker y su exposición segura a internet a través de Cloudflare Tunnels sobre una Raspberry Pi.

🚀 Características Principales
Seguridad Enterprise: Autenticación JWT con Spring Security y gestión de usuarios.

Lógica de Recurrencia: Sistema inteligente para manejar hábitos ONCE, DAILY, WEEKLY y MONTHLY.

Arquitectura de Red Segura: Exposición mediante túneles de Cloudflare (sin necesidad de abrir puertos en el router).

Contenerización: Despliegue agnóstico mediante Docker y Docker Compose.



🌐 Arquitectura de Despliegue (Self-Hosting)
El proyecto no solo es código, sino una solución de infraestructura completa:

Raspberry Pi: Servidor local que aloja los contenedores.

Docker Compose: Orquestación del backend, frontend y la base de datos.

Cloudflare Tunnel: Crea un canal seguro entre la Raspberry Pi y la red de Cloudflare.

SSL/TLS: Cifrado de extremo a extremo gestionado por el certificado de Cloudflare.
