FROM node:lts-alpine AS build

#### make the 'app' folder the current working directory
WORKDIR /usr/src/app

#### copy both 'package.json' and 'package-lock.json' (if available)
COPY package*.json ./

#### install angular cli
RUN npm install -g @angular/cli@17

#### install project dependencies
RUN npm install

#### copy things
COPY . .

#### generate build --prod
RUN ng build

### STAGE 2: Run ###
FROM nginx:stable

COPY default.conf /etc/nginx/conf.d/default.conf

#### copy artifact build from the 'build environment'
COPY --from=build /usr/src/app/dist/frontend/browser/* /usr/share/nginx/html

EXPOSE 80
