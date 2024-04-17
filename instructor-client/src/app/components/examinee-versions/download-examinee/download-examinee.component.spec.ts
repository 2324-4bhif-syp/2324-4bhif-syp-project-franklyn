import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DownloadExamineeComponent } from './download-examinee.component';

describe('DownloadExamineeComponent', () => {
  let component: DownloadExamineeComponent;
  let fixture: ComponentFixture<DownloadExamineeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DownloadExamineeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(DownloadExamineeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
