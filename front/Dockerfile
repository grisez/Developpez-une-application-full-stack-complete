FROM node:22-alpine AS build
WORKDIR /app
# The image only serves the built application: the Cypress binary used by the end-to-end tests is not needed
ENV CYPRESS_INSTALL_BINARY=0
COPY package.json package-lock.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

FROM nginx:1.27-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/dist/mdd-client/browser /usr/share/nginx/html
EXPOSE 80
