import { Injectable } from '@angular/core';
import {WebApiService} from "../web-api.service";
import {ExamineeDto} from "../entity/ExamineeDto";
import {Subject} from "rxjs";
import {Examinee} from "../entity/Examinee";

@Injectable({
  providedIn: 'root'
})
export class ExamineeDtoFilterService {
  private examinee = new Subject<[Examinee, number] | undefined>();

  constructor(private webApi: WebApiService) {}

  public determineIpForExaminees(examineeDto: ExamineeDto, idx: number) {
    for (const ip of examineeDto.ipAddresses) {
      this.webApi.getOpenBoxImage(ip).subscribe({
          next: () => this.examinee.next([new Examinee(examineeDto.username, ip, examineeDto.connected), idx]),
      })
    }

    return this.examinee.asObservable();
  }
}
