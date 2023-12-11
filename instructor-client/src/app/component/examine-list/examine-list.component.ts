import {Component, Injectable} from '@angular/core';
import {CommonModule} from "@angular/common";
import {ExamineComponent} from "../examine/examine.component";
import ExamineDataService from "../../shared/repository/examine-data.service";

@Injectable({
  providedIn: 'root'
})
@Component({
  selector: 'app-examine-list',
  standalone: true,
  imports: [
    CommonModule,
    ExamineComponent
  ],
  templateUrl: './examine-list.component.html',
  styleUrl: './examine-list.component.css'
})
export class ExamineListComponent {
  constructor(protected examineRepo: ExamineDataService) {
  }
}
