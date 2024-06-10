package com.algoExpert.demo.Admin.Repository;

import com.algoExpert.demo.Admin.Entity.FeatureUsage;
import com.algoExpert.demo.Admin.AdminEnums.FeatureType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface FeatureUsageRepository extends JpaRepository<FeatureUsage,Integer> {

    FeatureUsage  findByFeatureType(FeatureType featureType);

    // Custom query to count the number of CREATE_PROJECT entries
    @Query("SELECT COUNT(f) FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countProjectUsage(@Param("featureType") FeatureType featureType);

    // Custom query to get the usageCount for a specific featureType
    @Query("SELECT f.usageCount FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countTableUsage(@Param("featureType") FeatureType featureType);


    @Query("SELECT f.usageCount FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countTaskUsage(@Param("featureType") FeatureType featureType);



    @Query("SELECT f.usageCount FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countDuplicateUsage(@Param("featureType") FeatureType featureType);


    @Query("SELECT f.usageCount FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countDeleteTaskUsage(@Param("featureType") FeatureType featureType);


    @Query("SELECT f.usageCount FROM FeatureUsage f WHERE f.featureType = :featureType")
    Integer countSortUsage(@Param("featureType") FeatureType featureType);






}
