FROM node:21 AS builder

WORKDIR /opt/franklyn

COPY . .

RUN npm install
RUN npx ng build --configuration production --base-href /instructor/ instructor-client

FROM nginx:1.25.4

COPY --from=builder /opt/franklyn/dist/instructor-client/browser/* /usr/share/nginx/html/instructor/
COPY docker/nginx.conf /etc/nginx/nginx.conf
