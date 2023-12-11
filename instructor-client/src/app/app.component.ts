import {Component, Injectable} from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterOutlet } from '@angular/router';
import {ExamineComponent} from "./component/examine/examine.component";
import ExamineDataService from "./shared/repository/examine-data.service";
import {ExamineListComponent} from "./component/examine-list/examine-list.component";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterOutlet, ExamineComponent, ExamineListComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  constructor(protected examineRepo: ExamineDataService) {
  }
}
