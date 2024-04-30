import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ExamineeListComponent } from './examinee-list.component';

describe('ExamineeListComponent', () => {
  let component: ExamineeListComponent;
  let fixture: ComponentFixture<ExamineeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ExamineeListComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(ExamineeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
