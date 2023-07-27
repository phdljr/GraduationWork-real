import { createRouter, createWebHistory } from "vue-router";
// import MainView from "../views/MainView.vue";
import LoginView from "../views/LoginView.vue";
import RegisterView from "../views/RegisterView.vue";
import RoomListView from "../views/RoomListView.vue";
import OrderListView from "../views/OrderListView.vue";
import ReportView from "../views/ReportView.vue";
import CreateRoomView from "../views/CreateRoomView.vue";
import CashView from "../views/CashView.vue";
import KakaoPaySuccessView from "../views/KakaoPaySuccessView.vue";
import MyPageView from "../views/MyPageView.vue";

const routes = [
  {
    path: "/",
    name: "RoomListView",
    component: RoomListView,
  },
  {
    path: "/loginView",
    name: "LoginView",
    component: LoginView,
  },
  {
    path: "/register",
    name: "RegisterView",
    component: RegisterView,
  },
  {
    path: "/roomlist",
    name: "RoomListView",
    component: RoomListView,
  },
  {
    path: "/createroom",
    name: "CreateRoomView",
    component: CreateRoomView,
  },
  {
    path: "/orderlist",
    name: "OrderListView",
    component: OrderListView,
  },
  {
    path: "/report",
    name: "ReportView",
    component: ReportView,
  },
  {
    path: "/cash",
    name: "CashView",
    component: CashView,
  },
  {
    path: "/kakaoPaySuccessView",
    name: "KakaoPaySuccessView",
    component: KakaoPaySuccessView,
  },
  {
    path: "/mypageView",
    name: "MyPageView",
    component: MyPageView,
  },
];

const router = createRouter({
  history: createWebHistory(process.env.BASE_URL),
  routes,
});

export default router;
