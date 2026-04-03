import {Observable} from 'rxjs';
import {Student} from '../../domain/entities/student';
import {PageResponse} from '../../domain/entities/page-response';

export interface StudentServicePort {
    getStudentsByOwner(ownerId: string, page: number, size: number): Observable<PageResponse<Student>>;
}
