FROM node:23-alpine3.21 AS build

WORKDIR /app

COPY apps/port-frontend/package*.json ./
RUN npm install
COPY apps/port-frontend/ .
RUN export NODE_OPTIONS=--openssl-legacy-provider && npm run build

FROM nginx:1.27.4-alpine

RUN rm /usr/share/nginx/html/*
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
