# eyTeacher
TFM. Webapp para comunicación entre profesores y alumnos enfocada en micro-aprendizaje.

# Inicio:
docker compose -f backend/compose.yaml -f frontend/compose.yaml -f ai/ai/docker-compose.yml --profile production up --build -d.

# Parada:
docker compose -f backend/compose.yaml -f frontend/compose.yaml -f ai/ai/docker-compose.yml --profile production stop