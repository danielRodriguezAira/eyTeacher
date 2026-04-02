# eyTeacher
TFM. Webapp para comunicación entre profesores y alumnos enfocada en micro-aprendizaje.

# Inicio:
docker compose --env-file .env -f traefik/compose.yaml --profile production up --build -d \
&& docker compose --env-file .env -f backend/compose.yaml --profile production up --build -d \
&& docker compose --env-file .env -f frontend/compose.yaml --profile production up --build -d \
&& cd ai && docker compose up --build -d && cd ..

# Parada:
docker compose -f traefik/compose.yaml --profile production stop \
&& docker compose -f backend/compose.yaml --profile production stop \
&& docker compose -f frontend/compose.yaml --profile production stop \
&& cd ai && docker compose stop && cd ..