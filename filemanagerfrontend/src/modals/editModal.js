import React from 'react';
import 'antd/dist/antd.css';
import { Input } from 'antd';
 
const EditModal = (props) => {

 
 return (
 <div>
 Name : <Input value={props.fileName} onChange={props.changeName}/>
</div>
 );
};

export default EditModal;
 