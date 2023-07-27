import { createRouter, createWebHistory } from "vue-router";

// const MainView = () => import("../views/MainView.vue");
const LoginView = () => import("../views/LoginView.vue");
const RegisterView = () => import("../views/RegisterView.vue");
const RoomListView = () => import("../views/RoomListView.vue");
const OrderListView = () => import("../views/OrderListView.vue");
const ReportView = () => import("../views/ReportView.vue");
const CreateRoomView = () => import("../views/CreateRoomView.vue");
const CashView = () => import("../views/CashView.vue");
const KakaoPaySuccessView = () => import("../views/KakaoPaySuccessView.vue");
const MyPageView = () => import("../views/MyPageView.vue")

const routes = [
  {
    Path: "/",
    redirect: "/main",
  },
  {
    path: "/main",
    component: RoomListView,
  },
  {
    path: "/loginView",
    component: LoginView,
  },
  {
    path: "/register",
    component: RegisterView,
  },
  {
    path: "/roomlist",
    component: RoomListView,
  },
  {
    path: "/createroom",
    component: CreateRoomView,
  },
  {
    path: "/orderlist",
    component: OrderListView,
  },
  {
    path: "/report",
    component: ReportView,
  },
  {
    path: "/cash",
    component: CashView,
  },
  {
    path: "/kakaoPaySuccessView",
    component: KakaoPaySuccessView,
  },
  {
    path: "/mypageView",
    name: "MyPageView",
    component: MyPageView,
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
