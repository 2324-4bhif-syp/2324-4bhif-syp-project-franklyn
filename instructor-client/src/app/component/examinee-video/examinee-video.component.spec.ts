import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineeVideoComponent } from './examinee-video.component';

describe('ExamineeVideoComponent', () => {
  let component: ExamineeVideoComponent;
  let fixture: ComponentFixture<ExamineeVideoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamineeVideoComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamineeVideoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
