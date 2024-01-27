import {Component, Injectable} from '@angular/core';
import { CommonModule } from '@angular/common';
import {RouterLinkActive, RouterOutlet, RouterModule, Router} from '@angular/router';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {PatrolModeComponent} from "./component/router-components/patrol-mode/patrol-mode.component";
import {VideoViewerComponent} from "./component/router-components/video-viewer/video-viewer.component";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [
    CommonModule,
    RouterOutlet,
    FormsModule,
    RouterModule,
    RouterLinkActive,
    PatrolModeComponent,
    VideoViewerComponent,
    ReactiveFormsModule
  ],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  public patrolModeActive: boolean = true;
  public videoViewerActive: boolean = false;

  public changeRoutePatrolMode() {
    this.patrolModeActive = true;
    this.videoViewerActive = false;
  }

  public changeRouteVideoViewer() {
    this.patrolModeActive = false;
    this.videoViewerActive = true;
  }
}
