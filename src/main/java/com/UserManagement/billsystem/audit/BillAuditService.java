package com.UserManagement.billsystem.audit;

import com.UserManagement.billsystem.entities.Bill;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BillAuditService {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Object[]> getBillAuditHistory(Long billId) {
        AuditReader auditReader = AuditReaderFactory.get(entityManager);

        return auditReader.createQuery()
                .forRevisionsOfEntity(Bill.class, false, true)
                .add(AuditEntity.id().eq(billId))
                .getResultList();
    }
}

