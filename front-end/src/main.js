import { createApp } from "vue";
import App from "./App.vue";
import router from "./js/router";
import { BootstrapVue3 } from "bootstrap-vue-3";
import "bootstrap/dist/css/bootstrap.css";
import "bootstrap-vue-3/dist/bootstrap-vue-3.css";
import { store } from "./store/store";
import BootstrapIcon from "@dvuckovic/vue3-bootstrap-icons";
import dotenv from "dotenv";
import Vue3Geolocation from 'vue3-geolocation';

dotenv.config();
const app = createApp(App);
app.use(Vue3Geolocation);
app.use(BootstrapVue3);
app.component("b-icon", BootstrapIcon);
app.use(store);
app.use(router).mount("#app");
app.config.globalProperties.HOST = "http://lcoalhost:8080"
