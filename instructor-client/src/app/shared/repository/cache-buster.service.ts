import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class CacheBusterService {
  private cachebustNum: number = 0;

  get cacheBusterNum(): number {
    return this.cachebustNum;
  }

  set cacheBusterNum(val: number) {
    if (val === Number.MAX_VALUE) {
      this.cachebustNum = 0;
    } else {
      this.cachebustNum = val;
    }
  }
}
