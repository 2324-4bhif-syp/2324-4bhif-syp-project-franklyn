import { Injectable } from '@angular/core';
import {WebApiService} from "../web-api.service";
import {ExamineeDto} from "../entity/ExamineeDto";
import {Subject} from "rxjs";
import {Examinee} from "../entity/Examinee";

@Injectable({
  providedIn: 'root'
})
export class ExamineeDtoFilterService {
  private examinee = new Subject<Examinee | undefined>();

  constructor(private webApi: WebApiService) {}

  public determineIpForExaminees(examineeDto: ExamineeDto) {
    for (const ip of examineeDto.ipAddresses) {
      this.webApi.getOpenBoxImage(ip).subscribe({
          next: () => this.examinee.next(new Examinee(examineeDto.username, ip, examineeDto.connected)),
          error: (_) => _,
      })
    }

    return this.examinee.asObservable();
  }
}
