import { TestBed } from '@angular/core/testing';

import { ExamineeDataService } from './examinee-data.service';

describe('ExamineeDataService', () => {
  let service: ExamineeDataService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExamineeDataService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
