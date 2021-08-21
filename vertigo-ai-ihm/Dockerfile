FROM node:16.5 as build-stage

WORKDIR /app

COPY package*.json ./

RUN npm install -g @quasar/cli
RUN npm install
COPY ./ .

RUN quasar build


FROM nginx:1.21.1 as production-stage

RUN mkdir /app

COPY --from=build-stage /app/dist/spa /app
COPY nginx.conf /etc/nginx/nginx.conf