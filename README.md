# eyTeacher
TFM. Webapp para comunicación entre profesores y alumnos enfocada en micro-aprendizaje.

# Inicio:
docker compose -f backend/compose.yaml -f frontend/compose.yaml --profile production up --build -d
cd ai && docker compose up --build -d && cd ..

# Parada:
docker compose -f backend/compose.yaml -f frontend/compose.yaml --profile production stop
cd ai && docker compose stop && cd ..