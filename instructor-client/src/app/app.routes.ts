import { Routes } from '@angular/router';
import {PatrolModeComponent} from "./component/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./component/router-components/video-viewer/video-viewer.component";

export const routes: Routes = [
  {path: "", redirectTo: "patrol-mode", pathMatch: "full"},
  {path: "patrol-mode", component: PatrolModeComponent},
  {path: "video-viewer", component: VideoViewerComponent}
];
