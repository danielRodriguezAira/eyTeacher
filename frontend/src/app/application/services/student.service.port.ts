import {Observable} from 'rxjs';
import {Student} from '../../domain/entities/student';

export interface StudentServicePort {
    getStudentsByOwner(ownerId: string): Observable<Student[]>;
}
