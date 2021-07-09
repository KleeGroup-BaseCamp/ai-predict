FROM node:latest as build-stage
WORKDIR /app
COPY package*.json ./
RUN npm install -g @quasar/cli
RUN npm install
COPY ./ .
RUN quasar build

FROM nginx as production-stage
RUN mkdir /app
COPY --from=build-stage /app/dist/spa /app
COPY nginx.conf /etc/nginx/nginx.conf