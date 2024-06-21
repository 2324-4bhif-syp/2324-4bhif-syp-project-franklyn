import { Routes } from '@angular/router';
import {PatrolModeComponent} from "./components/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./components/router-components/video-viewer/video-viewer.component";
import {MetricsDashboardComponent} from "./components/router-components/metrics-dashboard/metrics-dashboard.component";
import {TestOverviewComponent} from "./components/router-components/test-overview/test-overview.component";

export const routes: Routes = [
  {path: "", redirectTo: "test-overview", pathMatch: "full"},
  {path: "test-overview", component: TestOverviewComponent},
  {path: "patrol-mode", component: PatrolModeComponent},
  {path: "test-overview/video-viewer", component: VideoViewerComponent},
  {path: "metrics-dashboard", component: MetricsDashboardComponent}
];
