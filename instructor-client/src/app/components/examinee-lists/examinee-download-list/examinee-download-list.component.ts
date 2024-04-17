import {Component, inject} from '@angular/core';
import {environment} from "../../../../../env/environment";
import {DownloadExamineeComponent} from "../../examinee-versions/download-examinee/download-examinee.component";
import {StoreService} from "../../../services/store.service";
import {distinctUntilChanged, map} from "rxjs";
import {AsyncPipe} from "@angular/common";

@Component({
  selector: 'app-examinee-download-list',
  standalone: true,
  imports: [
    DownloadExamineeComponent,
    AsyncPipe
  ],
  templateUrl: './examinee-download-list.component.html',
  styleUrl: './examinee-download-list.component.css'
})
export class ExamineeDownloadListComponent {
  protected examinees = inject(StoreService)
    .store
    .pipe(
      map(model => model.examineeData.examinees),
      distinctUntilChanged()
    );

  getDownloadUrl(): string {
    return `${environment.serverBaseUrl}/video/download`
  }
}
