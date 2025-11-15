import { Component, inject, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { SongsService } from '../song.service';


@Component({
  selector: 'app-songs-form',
  templateUrl: './songs-form.component.html',
  standalone: true,
  imports: [ReactiveFormsModule]
})
export default class SongsFormComponent implements OnInit {
  private songsService = inject(SongsService);
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);
  private router = inject(Router);

  form!: FormGroup;
  songId: string | null = null;

  ngOnInit() {
    this.form = this.fb.group({
      title: [''],
      artist: [''],
      genre: [''],
      year: ['']
    });

    this.songId = this.route.snapshot.paramMap.get('id');
    if (this.songId) {
      this.songsService.getById(this.songId).subscribe(song => {
        if (song) this.form.patchValue(song);
      });
    }
  }

  submit() {
    if (this.songId) {
      this.songsService.update(this.songId, this.form.value).subscribe(() => this.router.navigate(['/admin/songs']));
    } else {
      this.songsService.create(this.form.value).subscribe(() => this.router.navigate(['/admin/songs']));
    }
  }
}
