export class CacheBuster {
  constructor(cachebustNum: number) {
    this.cachebustNum = cachebustNum;
  }

  private cachebustNum: number;

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
