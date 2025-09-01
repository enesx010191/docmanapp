import { type RouteConfig, index, route } from "@react-router/dev/routes";

export default [
  index("routes/home.tsx"), 
  route("/login", "routes/login.tsx"),
  route("/register", "routes/register.tsx"),
  route("/edit-document", "routes/edit-document.tsx")
] satisfies RouteConfig;
