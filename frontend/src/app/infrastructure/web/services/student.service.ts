import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Student} from '../../../domain/entities/student';
import {StudentServicePort} from '../../../application/services/student.service.port';
import {PageResponse} from '../../../domain/entities/page-response';

@Injectable({
    providedIn: 'root'
})
export class StudentService implements StudentServicePort {
    private http = inject(HttpClient);
    private readonly API_URL = '/api/v1/auth';

    getStudentsByOwner(ownerId: string, page: number, size: number): Observable<PageResponse<Student>> {
        return this.http.get<PageResponse<Student>>(`${this.API_URL}/students/owner/${ownerId}`, {params: {page, size}});
    }
}
