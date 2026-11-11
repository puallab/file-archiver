alter table inspection_file drop constraint uk_inspection_file_product_id_inspected_at;
alter table inspection_file add constraint uk_inspection_file_file_path unique (file_path);
alter table inspection_file alter column file_path type varchar(255);
