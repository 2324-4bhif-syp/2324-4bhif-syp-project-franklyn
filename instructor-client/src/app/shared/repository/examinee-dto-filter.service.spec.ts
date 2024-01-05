import { TestBed } from '@angular/core/testing';

import { ExamineeDtoFilterService } from './examinee-dto-filter.service';

describe('ExamineeDtoFilterService', () => {
  let service: ExamineeDtoFilterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ExamineeDtoFilterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
