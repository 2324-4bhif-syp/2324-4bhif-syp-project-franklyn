import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PatrolPageExamineeComponent } from './patrol-page-examinee.component';

describe('PatrolPageExamineeComponent', () => {
  let component: PatrolPageExamineeComponent;
  let fixture: ComponentFixture<PatrolPageExamineeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PatrolPageExamineeComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(PatrolPageExamineeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
