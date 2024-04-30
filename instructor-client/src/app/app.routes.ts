import { Routes } from '@angular/router';
import {PatrolModeComponent} from "./components/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./components/router-components/video-viewer/video-viewer.component";
import {MetricsDashboardComponent} from "./components/router-components/metrics-dashboard/metrics-dashboard.component";

export const routes: Routes = [
  {path: "", redirectTo: "patrol-mode", pathMatch: "full"},
  {path: "patrol-mode", component: PatrolModeComponent},
  {path: "video-viewer", component: VideoViewerComponent},
  {path: "metrics-dashboard", component: MetricsDashboardComponent}
];
