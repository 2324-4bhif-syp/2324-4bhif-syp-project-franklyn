import { Routes } from '@angular/router';
import {PatrolModeComponent} from "./component/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./component/router-components/video-viewer/video-viewer.component";
import {MetricsDashboardComponent} from "./component/router-components/metrics-dashboard/metrics-dashboard.component";

export const routes: Routes = [
  {path: "", redirectTo: "patrol-mode", pathMatch: "full"},
  {path: "patrol-mode", component: PatrolModeComponent},
  {path: "video-viewer", component: VideoViewerComponent},
  {path: "metrics-dashboard", component: MetricsDashboardComponent}
];
