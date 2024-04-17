import {Component, inject} from '@angular/core';
import {StoreService} from "../../../services/store.service";
import {distinctUntilChanged, map} from "rxjs";
import {AsyncPipe} from "@angular/common";
import {PatrolPageExamineeComponent} from "../../examinee-versions/patrol-page-examinee/patrol-page-examinee.component";

@Component({
  selector: 'app-examinee-list',
  standalone: true,
  imports: [
    AsyncPipe,
    PatrolPageExamineeComponent
  ],
  templateUrl: './examinee-list.component.html',
  styleUrl: './examinee-list.component.css'
})
export class ExamineeListComponent {
  protected examinees = inject(StoreService)
    .store
    .pipe(
      map(model => model.examineeData.examinees),
      distinctUntilChanged()
    );
}
