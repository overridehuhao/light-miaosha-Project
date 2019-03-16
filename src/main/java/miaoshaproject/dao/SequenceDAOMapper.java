package miaoshaproject.dao;

import miaoshaproject.dataobject.SequenceDAO;

public interface SequenceDAOMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    int deleteByPrimaryKey(String name);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    int insert(SequenceDAO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    int insertSelective(SequenceDAO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    SequenceDAO selectByPrimaryKey(String name);

    SequenceDAO getSequenceByName(String name);
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    int updateByPrimaryKeySelective(SequenceDAO record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table sequence_info
     *
     * @mbggenerated Sat Mar 16 00:34:24 CST 2019
     */
    int updateByPrimaryKey(SequenceDAO record);
}